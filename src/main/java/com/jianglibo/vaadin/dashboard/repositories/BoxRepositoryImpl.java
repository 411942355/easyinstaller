package com.jianglibo.vaadin.dashboard.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jianglibo.vaadin.dashboard.domain.Box;
import com.jianglibo.vaadin.dashboard.domain.Box_;
import com.jianglibo.vaadin.dashboard.util.JpqlUtil;

public class BoxRepositoryImpl implements BoxRepositoryCustom<Box> {
	
	
	@Autowired
	private JpqlUtil jpqjUtil;


	@Override
	public List<Box> getFilteredPageWithOnePhrase(Pageable page, String filterString, boolean trashed, Sort sort) {
		return jpqjUtil.getFilteredPage(Box.class, page, filterString, trashed, sort, Box_.name.getName(), Box_.ip.getName());
	}

	@Override
	public long getFilteredNumberWithOnePhrase(String filterString, boolean trashed) {
		return jpqjUtil.getFilteredNumber(Box.class, filterString, trashed, Box_.name.getName(), Box_.ip.getName());
	}

}
