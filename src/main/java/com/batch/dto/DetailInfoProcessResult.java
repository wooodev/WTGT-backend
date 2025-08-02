package com.batch.dto;

import java.util.List;

import com.swyp.catsgotogedog.content.domain.entity.batch.recur.RecurInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.recur.RecurInformationRoom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DetailInfoProcessResult {
	private final List<RecurInformation> generalInfoList;
	private final List<RecurInformationRoom> roomInfoList;

}
