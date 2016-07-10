package com.jianglibo.vaadin.dashboard.repositories;


import javax.persistence.EntityManager;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.google.common.collect.Lists;
import com.jianglibo.vaadin.dashboard.domain.ShellExecRole;


public class ShellExecRoleRepositoryImpl  extends SimpleJpaRepository<ShellExecRole, Long> implements ShellExecRoleRepositoryCustom, ApplicationContextAware {
    @SuppressWarnings("unused")
    private EntityManager entityManager;

    @SuppressWarnings("unused")
    private ApplicationContext context;


    /**
     * @param domainClass
     * @param em
     */
    @Autowired
    public ShellExecRoleRepositoryImpl(EntityManager entityManager) {
        super(ShellExecRole.class, entityManager);
        this.entityManager = entityManager;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
    
    public Page<ShellExecRole> differentUser(Long uid, Pageable pageable) {
        if (uid == -1) {
            return super.findAll(pageable);
        } else {
            return new PageImpl<>(Lists.newArrayList());
        }
    }



}
