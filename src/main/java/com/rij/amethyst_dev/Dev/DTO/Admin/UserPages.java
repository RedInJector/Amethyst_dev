package com.rij.amethyst_dev.Dev.DTO.Admin;

import com.rij.amethyst_dev.Dev.DTO.User.UserDataDTO;

import java.util.List;

public record UserPages(
        List<UserDataDTO> users,
        int currentPage,
        int maxPage
) {
}
