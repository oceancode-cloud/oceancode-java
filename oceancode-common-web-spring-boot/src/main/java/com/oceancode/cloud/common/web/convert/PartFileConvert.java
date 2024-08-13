package com.oceancode.cloud.common.web.convert;

import com.oceancode.cloud.common.entity.PartFile;
import com.oceancode.cloud.common.util.FileUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

public class PartFileConvert implements Converter<MultipartFile, PartFile> {
    @Override
    public PartFile convert(MultipartFile source) {
        return FileUtil.getPartFile(source);
    }
}
