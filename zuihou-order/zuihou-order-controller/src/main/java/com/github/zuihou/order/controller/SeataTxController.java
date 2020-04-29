package com.github.zuihou.order.controller;

import com.github.zuihou.base.R;
import com.github.zuihou.demo.entity.Product;
import com.github.zuihou.order.api.DemoTestApi;
import com.github.zuihou.order.entity.Order;
import com.github.zuihou.order.service.OrderService;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分布式事务测试类
 *
 * @author zuihou
 * @date 2019/08/12
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/seata")
@Api(value = "SeataTxController", tags = "分布式事务测试类")
public class SeataTxController
{

    @Autowired
    private OrderService orderService;
    @Autowired
    private DemoTestApi seataTestApi;


    /**
     * 正常的提交事务
     *
     * @param data
     * @return
     */
    @PostMapping("/save")
    @GlobalTransactional
    public R<Product> saveCommitSuccess(@RequestBody Product data)
    {
        log.info("data={}", data);
        this.seataTestApi.save(data);
        Order entity = Order.builder().code(data.getName() + "CODE").name(data.getName()).build();
        this.orderService.save(entity);
        return R.success(data);
    }

    /**
     * 失败的回滚！  因为没有 @GlobalTransactional 注解
     *
     * @param data
     * @return
     */
    @PostMapping("/save/rollback/fail")
    public R<Product> saveRollbackFail(@RequestBody Product data)
    {
        log.info("data={}", data);
        this.seataTestApi.save(data);
        int i = 1 / 0;
        Order entity = Order.builder().code(data.getName() + "CODE").name(data.getName()).build();
        this.orderService.save(entity);
        return R.success(data);
    }

    /**
     * 正常回滚, 是分布式事务回滚的效果
     * <p>
     * 目前存在2个问题：
     * 1， seata-all 1.0.0 SeataHystrixConcurrencyStrategy 和 本项目中 ThreadLocalHystrixConcurrencyStrategy 冲突，导致事务回滚失效。
     * 临时解决方案是将 ThreadLocalHystrixConcurrencyStrategy 中内部类 WrappedCallable 中注释的4行代码释放开
     * <p>
     * 2，insert into zuihou_base_0000.m_product 语法，还是无法回滚， 想要测试，请勿在请求头中传递 tenant 参数，并将m_product表和m_order 表复制到 zuihou_defaults
     *
     * @param
     * @return
     */
    @PostMapping("/save/rollback")
    @GlobalTransactional
    public Boolean placeOrderRollback()
    {
        Product data = Product.builder().name("你的名字").stock(123).build();
        log.info("data={}", data);
        R<Product> save = this.seataTestApi.save(data);

        //在这里打断点可以看到 m_product 表的数据已经插入
        //但等执行完整个方法，发现 m_product 数据被删除
        int i = 1 / 0;
        Order entity = Order.builder().code(data.getName() + "code").name(data.getName()).build();
        this.orderService.save(entity);

        return true;
    }

    /**
     * 正常回滚， 是demo服务的本地事务回滚的效果
     *
     * @param data
     * @return
     */
    @PostMapping("/save/rollback2")
    @GlobalTransactional
    public R<Product> saveRollbackSuccess2(@RequestBody Product data)
    {
        log.info("data={}", data);
        this.seataTestApi.saveEx(data);

        Order entity = Order.builder().code(data.getName() + "CODE").name(data.getName()).build();
        this.orderService.save(entity);
        return R.success(data);
    }
}
