package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.dto.RoleDTO;
import com.nixmash.blog.jpa.dto.UserDTO;
import com.nixmash.blog.jpa.dto.UserPasswordDTO;
import com.nixmash.blog.jpa.enums.ResetPasswordResult;
import com.nixmash.blog.jpa.enums.Role;
import com.nixmash.blog.jpa.model.Authority;
import com.nixmash.blog.jpa.model.CurrentUser;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.model.UserConnection;
import com.nixmash.blog.jpa.model.UserData;
import com.nixmash.blog.jpa.model.UserToken;
import com.nixmash.blog.jpa.repository.AuthorityRepository;
import com.nixmash.blog.jpa.repository.UserConnectionRepository;
import com.nixmash.blog.jpa.repository.UserDataRepository;
import com.nixmash.blog.jpa.repository.UserRepository;
import com.nixmash.blog.jpa.repository.UserTokenRepository;
import com.nixmash.blog.jpa.service.interfaces.UserService;
import com.nixmash.blog.jpa.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;


    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserById(long id) {
        log.debug("Getting user={}", id);
        return Optional.ofNullable(userRepository.findById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByUsername(String username) {
        log.debug("Getting user={}", username);
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getByEmail(String email) {
        log.debug("Getting user by email={}", email);
        return userRepository.findOneByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getByUserKey(String userKey) {
        log.debug("Getting user by userkey={}", userKey);
        return userRepository.findOneByUserKey(userKey);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<User> getAllUsers() {
        log.debug("Getting all users");
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User create(UserDTO userDTO) {

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        user.setUserKey(RandomStringUtils.randomAlphanumeric(16));
        user.setSignInProvider(userDTO.getSignInProvider());
        user.setEnabled(userDTO.isEnabled());

        User saved = userRepository.save(user);

        UserData userData = userDataRepository.save(UserUtils.newRegisteredUserData(saved));
        saved.setUserData(userData);

        for (Authority authority: userDTO.getAuthorities()) {
            Authority _authority = authorityRepository.findByAuthority(authority.getAuthority());
            saved.getAuthorities().add(_authority);
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Authority> getRoles() {
        return authorityRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersWithDetail() {
        return userRepository.getUsersWithDetail();
    }

    @Override
    public boolean canAccessUser(CurrentUser currentUser, String username) {
        log.info("Checking if user={} has access to user={}", currentUser, username);
        return currentUser != null
                && (currentUser.getUser().hasAuthority(Role.ROLE_ADMIN) ||
                currentUser.getUsername().equals(username));
    }

    @Transactional(readOnly = true)
    @Override
    public UserConnection getUserConnectionByUserId(String userId) {
        log.debug("Getting userConnection={}", userId);
        return userConnectionRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public ResetPasswordResult updatePassword(UserPasswordDTO userPasswordDTO) {
        boolean isLoggedIn = userPasswordDTO.getUserId() > 0;
        User user = null;
        Optional<UserToken> userToken = Optional.empty();
        if (isLoggedIn)
            user = userRepository.findById(userPasswordDTO.getUserId());
        else {
            userToken =
                    userTokenRepository.findByToken(userPasswordDTO.getVerificationToken());

            if (userToken.isPresent()) {
                user = userToken.get().getUser();
                if (!isValidToken(user.getId(), userToken.get().getToken())) {
                    user = null;
                }
            }
        }

        if (user == null)
            return ResetPasswordResult.ERROR;
        else {
            user.setPassword(UserUtils.bcryptedPassword(userPasswordDTO.getPassword()));
            if (userToken.isPresent()) {
                userTokenRepository.delete(userToken.get());
            }
        }

        if (isLoggedIn)
            return ResetPasswordResult.LOGGEDIN_SUCCESSFUL;
        else
            return ResetPasswordResult.FORGOT_SUCCESSFUL;
    }

    @Transactional
    @Override
    public UserToken createUserToken(User user) {
        Optional<UserToken> userToken = userTokenRepository.findByUserId(user.getId());
        if (userToken.isPresent())
            userToken.get().updateToken(UUID.randomUUID().toString());
        else
            userToken = Optional.of(new UserToken(UUID.randomUUID().toString(), user));

        return userTokenRepository.save(userToken.get());
    }

    @Transactional
    @Override
    public Optional<UserToken> getUserToken(String token) {
        return userTokenRepository.findByToken(token);
    }

    @Transactional
    @Override
    public User update(UserDTO userDTO) {

        User user = userRepository.findById(userDTO.getUserId());
        user.update(userDTO.getUsername(), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        if (userDTO.isUpdateChildren()) {

            user.getAuthorities().clear();
            for (Authority authority: userDTO.getAuthorities()) {
                Authority match = authorityRepository.findOne(authority.getId());
                if (!user.getAuthorities().contains(match)) {
                    user.getAuthorities().add(match);
                }
            }
        }
        return user;
    }

    @Transactional
    @Override
    public User enableAndApproveUser(User user) {

        UserData userData = user.getUserData();
        userData.setApprovedDatetime(Calendar.getInstance().getTime());

        user.setEnabled(true);
        user.update(userData);

        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByIdWithDetail(Long ID) {
        return userRepository.findByUserIdWithDetail(ID);
    }

    // region Authorities

    @Transactional
    @Override
    public Authority createAuthority(RoleDTO roleDTO) {
        Authority authority = new Authority();
        authority.setAuthority(roleDTO.getAuthority());
        return authorityRepository.save(authority);
    }

    @Transactional
    @Override
    public Authority updateAuthority(RoleDTO roleDTO) {
        Authority authority = authorityRepository.findOne(roleDTO.getId());
        authority.setAuthority(roleDTO.getAuthority());
        return authority;
    }

    @Override
    public Authority getAuthorityById(Long id) {
        return authorityRepository.findOne(id);
    }

    @Transactional
    @Override
    public void deleteAuthority(Authority authority, Collection<User> users) {
        if (users != null) {
            for (User user: users) {
                user.getAuthorities().remove(authority);
            }
        }
        authorityRepository.delete(authority);
    }

    @Override
    public Collection<User> getUsersByAuthorityId(Long authorityId) {
        return userRepository.findByAuthorityId(authorityId);

    }

    @Transactional
    @Override
    public User updateHasAvatar(Long userId, boolean hasAvatar) {
        User user = userRepository.findById(userId);
        user.setHasAvatar(hasAvatar);

        CurrentUser currentUser = new CurrentUser(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        currentUser,
                        user.getPassword(),
                        user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }
    // endregion

    // region Utility methods

    @Override
    public boolean isValidToken(long userId, String token) {
        final Optional<UserToken> userToken = userTokenRepository.findByToken(token);
        boolean isValidToken = false;
        if (userToken.isPresent()) {
            final Calendar cal = Calendar.getInstance();
            UserToken passToken = userToken.get();

            if (passToken.getUser().getId().equals(userId) && (passToken.getTokenExpiration().getTime() - cal.getTime().getTime()) > 0) {
                isValidToken = true;
            }
        }
        return isValidToken;
    }

    // endregion
}


