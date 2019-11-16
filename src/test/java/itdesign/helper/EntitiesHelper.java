package itdesign.helper;

import itdesign.entity.Group;
import itdesign.entity.Slice;
import itdesign.entity.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public final class EntitiesHelper {
	public final static String STATUS_CODE="1";
	public final static String STATUS_NAME="Статус 1";

	public final static String GROUP_CODE="01";
	public final static String GROUP_NAME="Группа 1";

	public final static String SLICE_GROUP_CODE = "01";
	public final static String SLICE_STATUS_CODE = "0";
	public final static String SLICE_REGION = "19";
	public final static LocalDate SLICE_START_DATE = LocalDate.of(2019, 1, 1);
	public final static LocalDate SLICE_END_DATE = LocalDate.of(2019, 1, 31);
	public final static LocalDateTime SLICE_CREATED_DATE = LocalDateTime.now().of(2019, 1, 31, 23, 59, 59);;
	public final static Long SLICE_MAX_REC_NUM = 9999l;

	public final static String STATUS_DELETED_CODE = "3";
	public final static String STATUS_DEFAULT_CODE = "0";
	public final static String STATUS_DEFAULT_NAME = "Статус 0";

	public static Status newStatus() {
		Status status = new Status();
		status.setId(1l);
		status.setCode(STATUS_CODE);
		status.setName(STATUS_NAME);
		return status;
	}
	
	public static Status newStatus(Long id) {
		Status status = newStatus();
		status.setId(id);
		return status;
	}

	public static void assertStatus(Status status) {
		assertNotNull(status);
		assertNotNull(status.getId());
		assertTrue(status.getId()>0);
		assertEquals(STATUS_NAME, status.getName());
		assertEquals(STATUS_CODE, status.getCode());
	}

	public static Group newGroup() {
		Group newGroup = new Group();
		newGroup.setId(1l);
		newGroup.setCode(GROUP_CODE);
		newGroup.setName(GROUP_NAME);
		return newGroup;
	}

	public static Group newGroup(Long id) {
		Group group = newGroup();
		group.setId(id);
		return group;
	}

	public static void assertGroup(Group group) {
		assertNotNull(group);
		assertNotNull(group.getId());
		assertTrue(group.getId()>0);
		assertEquals(GROUP_CODE, group.getCode());
		assertEquals(GROUP_NAME, group.getName());
	}

	public static Slice newSlice() {
		Slice slice = new Slice();
		slice.setId(1l);
		slice.setStartDate(SLICE_START_DATE);
		slice.setEndDate(SLICE_END_DATE);
		slice.setCreatedDate(SLICE_CREATED_DATE);
		slice.setRegion(SLICE_REGION);
		slice.setMaxRecNum(SLICE_MAX_REC_NUM);
		slice.setGroupCode(SLICE_GROUP_CODE);
		slice.setStatusCode(SLICE_STATUS_CODE);
		return slice;
	}

	public static Slice newSlice(Long id) {
		Slice slice = newSlice();
		slice.setId(id);
		return slice;
	}

	public static void assertSlice(Slice slice) {
		assertNotNull(slice);
		assertNotNull(slice.getId());
		assertTrue(slice.getId()>0);
		assertEquals(SLICE_START_DATE, slice.getStartDate());
		assertEquals(SLICE_END_DATE, slice.getEndDate());
		assertEquals(SLICE_CREATED_DATE, slice.getCreatedDate());
		assertEquals(SLICE_REGION, slice.getRegion());
		assertEquals(SLICE_MAX_REC_NUM, slice.getMaxRecNum());

		assertNotNull(slice.getGroupCode());
		assertEquals(SLICE_GROUP_CODE, slice.getGroupCode());

		assertNotNull(slice.getStatusCode());
		assertEquals(SLICE_STATUS_CODE, slice.getStatusCode());
	}
}
