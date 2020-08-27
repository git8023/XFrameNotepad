package org.y.notepad.service;

import org.y.notepad.model.result.SearchResult;

import java.util.List;

/**
 * 搜索服务
 */
public interface SearchService {

    /**
     * 在当前用户目录下查找目录/记事本名称
     *
     * @param userId 用户ID
     * @param key    关键字
     * @return 搜索结果
     */
    List<SearchResult> atUserPath(int userId, String key);

}
