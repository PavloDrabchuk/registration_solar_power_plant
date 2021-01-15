package com.example.demo.service;

import com.example.demo.dao.UsersRepository;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = usersRepository.findByUsername(username);
        if (user.isEmpty()) throw new UsernameNotFoundException(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        /*for (UserRole role : user.getUserRole()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }*/



        grantedAuthorities.add(new SimpleGrantedAuthority(user.get().getUserRoles().toString()));
        System.out.println("access: "+user.get().getUserRoles().toString());

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), grantedAuthorities);
    }
}
