package org.y.notepad.model.condition;

import lombok.Data;
import org.y.notepad.model.enu.ModuleStatus;
import org.y.notepad.util.page.PageCondition;

/**
 * 模块分页过滤条件
 */
@Data
public class ModuleCondition extends PageCondition {
    private String name;
    private String author;
    private ModuleStatus status;
}
