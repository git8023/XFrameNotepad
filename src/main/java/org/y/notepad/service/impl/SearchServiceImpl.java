package org.y.notepad.service.impl;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.enu.SearchResultType;
import org.y.notepad.model.result.SearchResult;
import org.y.notepad.repository.DirectoryRepository;
import org.y.notepad.repository.NotepadRepository;
import org.y.notepad.service.SearchService;
import org.y.notepad.util.StringUtil;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final DirectoryRepository directoryRepository;
    private final NotepadRepository notepadRepository;

    public SearchServiceImpl(DirectoryRepository directoryRepository, NotepadRepository notepadRepository) {
        this.directoryRepository = directoryRepository;
        this.notepadRepository = notepadRepository;
    }

    @Override
    public List<SearchResult> atUserPath(int userId, String key) {
        key = StringUtil.trimToNull(key);
        key = StringUtil.convertVagueCondition(key);
        List<SearchResult> ret = Lists.newArrayList();

        // 搜索目录
        List<Directory> dirs = directoryRepository.MAPPER.selectListByPathKey(userId, key);
        dirs.forEach(dir -> ret.add(new SearchResult(dir.getId(), SearchResultType.DIRECTORY, dir)));

        // 搜索记事本名称
        List<Notepad> notepads = notepadRepository.MAPPER.selectListByNameKey(userId, key);
        notepads.forEach(notepad -> ret.add(new SearchResult(notepad.getId(), SearchResultType.NOTEPAD, notepad)));

        return ret;
    }

}
