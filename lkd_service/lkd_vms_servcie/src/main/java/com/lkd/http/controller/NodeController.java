package com.lkd.http.controller;

import com.lkd.entity.NodeEntity;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.http.controller.vo.Pager;
import com.lkd.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    // 分页查询
    @GetMapping("/search")
    public Pager<NodeEntity> search(
            @RequestParam(defaultValue = "1") Integer pageIndex,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name,
            Long regionId) {
        return nodeService.search(pageIndex, pageSize, name, regionId);
    }

    // 点位详情
    @GetMapping("/vmList/{id}")
    public List<VendingMachineEntity> nodeDetail(@PathVariable("id")Long id){
        return nodeService.nodeDetail(id);
    }
}