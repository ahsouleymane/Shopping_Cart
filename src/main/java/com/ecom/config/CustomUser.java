package com.ecom.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecom.model.UserDtls;

public class CustomUser implements UserDetails{
	
	private UserDtls user;

	public CustomUser(UserDtls user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
		return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
	@Override
    public boolean isAccountNonExpired() {
        return true;
    }
	
	@Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked(); // Or your implementation
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Or your implementation
    }

    @Override
    public boolean isEnabled() {
        return user.getIsEnable(); // Or your implementation
    }

}
