package org.y.notepad.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.result.Result;
import org.y.notepad.model.result.SearchResult;
import org.y.notepad.service.SearchService;
import org.y.notepad.web.util.WebUtil;

import java.util.List;

/**
 * 搜索控制器
 * 通过关键字搜索目录、记事本名称、记事本内容等
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 在当前用户目录下搜索
     *
     * @param key 关键字
     */
    @RequestMapping("/cup")
    private Result currentUserPath(String key) {
        User user = WebUtil.getUser();
        int userId = user.getId();
        List<SearchResult> srs = searchService.atUserPath(userId, key);
        return Result.data(srs);
    }

}
