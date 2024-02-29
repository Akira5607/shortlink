package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Juzi    2024/2/29 11:55
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
}
