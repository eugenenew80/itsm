package itdesign.helper;

import itdesign.entity.Group;
import itdesign.entity.Status;
import static org.junit.Assert.*;

public final class EntitiesHelper {
	public final static String STATUS_NAME="Статус 1";
	public final static String GROUP_NAME="Группа 1";

	public static Status newStatus() {
		Status status = new Status();
		status.setId(1l);
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
	}

	public static Group newGroup() {
		Group newGroup = new Group();
		newGroup.setId(1l);
		return newGroup;
	}

	public static Group newGroup(Long id) {
		Group group = new Group();
		group.setId(id);
		return group;
	}

	public static void assertGroup(Group group) {
		assertNotNull(group);
		assertNotNull(group.getId());
		assertTrue(group.getId()>0);
		assertEquals(GROUP_NAME, group.getName());
	}

}
