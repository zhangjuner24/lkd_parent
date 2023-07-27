package com.lkd.http.controller;

import com.lkd.entity.SkuEntity;
import com.lkd.file.FileManager;
import com.lkd.http.controller.vo.Pager;
import com.lkd.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/sku")
@Slf4j
public class SkuController {

    @Autowired
    private SkuService skuService;

    @GetMapping("/search")
    public Pager<SkuEntity> search(
            @RequestParam(defaultValue = "1") Integer pageIndex,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String skuName,
            Long classId
    ){
        return skuService.search(pageIndex, pageSize, skuName, classId);
    }

    @Autowired
    private FileManager fileManager;

    // 图片上传
    @PostMapping("/fileUpload")
    public String fileUpload(MultipartFile fileName) {
        String url = fileManager.uploadFile(fileName);
        return url;
    }

    //售货机


}