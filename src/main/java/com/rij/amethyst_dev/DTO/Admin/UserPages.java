package com.rij.amethyst_dev.DTO.Admin;

import com.rij.amethyst_dev.DTO.User.UserDataDTO;

import java.util.List;

public record UserPages(
        List<UserDataDTO> users,
        int currentPage,
        int maxPage
) {
}
