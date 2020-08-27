package org.y.notepad.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y.notepad.model.enu.SearchResultType;

/**
 * 搜索结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    /**
     * 数据ID
     */
    private int id;

    /**
     * 搜索结果类型
     */
    private SearchResultType type;

    /**
     * 搜索结果数据
     */
    private Object data;
}
