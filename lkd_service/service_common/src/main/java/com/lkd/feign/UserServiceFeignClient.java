package com.lkd.feign;
import com.lkd.http.controller.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "user-service")
public interface UserServiceFeignClient{


    /**
     * 获取运营员数量
     * @return
     */
    @GetMapping("/user/operaterCount")
    Integer getOperatorCount();

    /**
     * 获取维修员数量
     * @return
     */
    @GetMapping("/user/repairerCount")
    Integer getRepairerCount();
}