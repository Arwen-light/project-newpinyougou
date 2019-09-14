package com.pinyougou.cart.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 赋予登录用户角色的相关信息
        Collection<GrantedAuthority> authorirties  = new ArrayList<>();
        GrantedAuthority roles =  new SimpleGrantedAuthority("ROLE_USER");
        authorirties.add(roles);
        return new User(username,"suibian",authorirties);
    }
}
