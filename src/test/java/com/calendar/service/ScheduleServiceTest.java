package com.calendar.service;

import com.calendar.dto.schedule.ScheduleReq;
import com.calendar.dto.schedule.ScheduleResponse;
import com.calendar.entity.Schedule;
import com.calendar.entity.User;
import com.calendar.mapper.ScheduleMapper;
import com.calendar.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

	@Mock private ScheduleRepository scheduleRepository;
	@Mock private ScheduleMapper scheduleMapper;
	@InjectMocks private ScheduleService scheduleService;

	private User user;
	private User otherUser;

	@BeforeEach
	void setup() {
		user = User.builder()
				.email("test@test.com")
				.password("encoded1234")
				.nickname("tester")
				.build();
		otherUser = User.builder()
				.email("other@test.com")
				.password("encoded5678")
				.nickname("other")
				.build();
	}

	@Test
	void 일정_생성() {
		// given
		ScheduleReq req = ScheduleReq.builder()
				.title("회의")
				.description("주간 회의")
				.color("#FF0000")
				.startDate("2025-01-01")
				.startAt("2025-01-01T09:00")
				.endAt("2025-01-01T10:00")
				.alarmEnabled(true)
				.build();

		Schedule entity = Schedule.builder()
				.id("01JABCDETESTSCHEDULEID000001")
				.title("회의")
				.description("주간 회의")
				.color("#FF0000")
				.startDate(LocalDate.parse("2025-01-01"))
				.allDay(false)
				.alarmEnabled(true)
				.build();

		ScheduleResponse response = ScheduleResponse.builder()
				.id("01JABCDETESTSCHEDULEID000001")
				.title("회의")
				.description("주간 회의")
				.color("#FF0000")
				.startDate("2025-01-01")
				.alarmEnabled(true)
				.build();

		when(scheduleMapper.toEntity(req)).thenReturn(entity);
		when(scheduleRepository.save(any(Schedule.class))).thenReturn(entity);
		when(scheduleMapper.toResponse(entity)).thenReturn(response);

		// when
		ScheduleResponse result = scheduleService.create(req, user);

		// then
		assertEquals("회의", result.getTitle());
		verify(scheduleRepository).save(entity);
	}

	@Test
	void 단건조회_성공() {
		// given
		String id = "01JABCDETESTSCHEDULEID000002";
		Schedule entity = Schedule.builder().id(id).title("일정").color("#000000").user(user).startDate(LocalDate.parse("2025-01-02")).build();
		ScheduleResponse response = ScheduleResponse.builder().id(id).title("일정").color("#000000").startDate("2025-01-02").build();
		when(scheduleRepository.findById(id)).thenReturn(Optional.of(entity));
		when(scheduleMapper.toResponse(entity)).thenReturn(response);

		// when
		ScheduleResponse result = scheduleService.getById(id, user);

		// then
		assertEquals(id, result.getId());
	}

	@Test
	void 단건조회_권한없음() {
		// given
		String id = "01JABCDETESTSCHEDULEID000003";
		Schedule entity = Schedule.builder().id(id).title("일정").color("#000000").user(otherUser).startDate(LocalDate.parse("2025-01-03")).build();
		when(scheduleRepository.findById(id)).thenReturn(Optional.of(entity));

		// then
		assertThrows(IllegalArgumentException.class, () -> scheduleService.getById(id, user));
	}

	@Test
	void 특정날짜_조회() {
		// given
		LocalDate date = LocalDate.parse("2025-01-05");
		Schedule e1 = Schedule.builder().id("01JABCDETESTSCHEDULEID000011").title("A").color("#111111").user(user).startDate(date).build();
		Schedule e2 = Schedule.builder().id("01JABCDETESTSCHEDULEID000012").title("B").color("#222222").user(user).startDate(date).build();
		when(scheduleRepository.findByUserAndStartDate(user, date)).thenReturn(List.of(e1, e2));
		when(scheduleMapper.toResponse(e1)).thenReturn(ScheduleResponse.builder().id(e1.getId()).title("A").color("#111111").startDate(date.toString()).build());
		when(scheduleMapper.toResponse(e2)).thenReturn(ScheduleResponse.builder().id(e2.getId()).title("B").color("#222222").startDate(date.toString()).build());

		// when
		List<ScheduleResponse> list = scheduleService.getByDate(date, user);

		// then
		assertEquals(2, list.size());
	}

	@Test
	void 기간_조회() {
		// given
		LocalDate start = LocalDate.parse("2025-01-01");
		LocalDate end = LocalDate.parse("2025-01-31");
		Schedule e1 = Schedule.builder().id("01JABCDETESTSCHEDULEID000021").title("C").color("#333333").user(user).startDate(LocalDate.parse("2025-01-10")).build();
		when(scheduleRepository.findByUserAndStartDateBetween(user, start, end)).thenReturn(List.of(e1));
		when(scheduleMapper.toResponse(e1)).thenReturn(ScheduleResponse.builder().id(e1.getId()).title("C").color("#333333").startDate("2025-01-10").build());

		// when
		List<ScheduleResponse> list = scheduleService.getByPeriod(start, end, user);

		// then
		assertEquals(1, list.size());
		assertEquals("C", list.get(0).getTitle());
	}

	@Test
	void 수정_성공() {
		// given
		String id = "01JABCDETESTSCHEDULEID000031";
		Schedule existing = Schedule.builder().id(id).title("Old").description("old").color("#000000").user(user).startDate(LocalDate.parse("2025-01-01")).build();
		when(scheduleRepository.findById(id)).thenReturn(Optional.of(existing));

		ScheduleReq req = ScheduleReq.builder()
				.title("New")
				.description("new")
				.color("#FFFFFF")
				.startDate("2025-02-01")
				.startAt("2025-02-01T09:00")
				.endAt("2025-02-01T10:00")
				.alarmEnabled(true)
				.build();

		Schedule saved = Schedule.builder().id(id).title("New").description("new").color("#FFFFFF").user(user).startDate(LocalDate.parse("2025-02-01")).build();
		when(scheduleRepository.save(any(Schedule.class))).thenReturn(saved);
		when(scheduleMapper.toResponse(saved)).thenReturn(ScheduleResponse.builder().id(id).title("New").color("#FFFFFF").startDate("2025-02-01").build());

		// when
		ScheduleResponse result = scheduleService.update(id, req, user);

		// then
		assertEquals("New", result.getTitle());
		ArgumentCaptor<Schedule> captor = ArgumentCaptor.forClass(Schedule.class);
		verify(scheduleRepository).save(captor.capture());
		assertEquals("#FFFFFF", captor.getValue().getColor());
	}

	@Test
	void 수정_권한없음() {
		// given
		String id = "01JABCDETESTSCHEDULEID000032";
		Schedule existing = Schedule.builder().id(id).title("Old").color("#000000").user(otherUser).startDate(LocalDate.parse("2025-01-01")).build();
		when(scheduleRepository.findById(id)).thenReturn(Optional.of(existing));

		ScheduleReq req = ScheduleReq.builder().title("New").color("#FFFFFF").startDate("2025-02-01").build();

		// then
		assertThrows(IllegalArgumentException.class, () -> scheduleService.update(id, req, user));
		verify(scheduleRepository, never()).save(any());
	}

	@Test
	void 삭제_성공() {
		// given
		String id = "01JABCDETESTSCHEDULEID000041";
		Schedule existing = Schedule.builder().id(id).title("Old").color("#000000").user(user).startDate(LocalDate.parse("2025-01-01")).build();
		when(scheduleRepository.findById(id)).thenReturn(Optional.of(existing));

		// when
		scheduleService.delete(id, user);

		// then
		verify(scheduleRepository).delete(existing);
	}

	@Test
	void 삭제_권한없음() {
		// given
		String id = "01JABCDETESTSCHEDULEID000042";
		Schedule existing = Schedule.builder().id(id).title("Old").color("#000000").user(otherUser).startDate(LocalDate.parse("2025-01-01")).build();
		when(scheduleRepository.findById(id)).thenReturn(Optional.of(existing));

		// then
		assertThrows(IllegalArgumentException.class, () -> scheduleService.delete(id, user));
		verify(scheduleRepository, never()).delete(any());
	}
}
