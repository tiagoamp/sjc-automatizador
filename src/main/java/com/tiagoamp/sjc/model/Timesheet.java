package com.tiagoamp.sjc.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Timesheet {

	private List<LocalDate> holidays;
	private int totalExtraHours;
	private LocalDate startDate, endDate;
	
	private int HOURS_PER_DAY = 8, DEFAULT_EXTRA_HOURS_PER_DAY = 2;
	
	private Map<LocalDate, Integer> hoursPerDay;
	private Map<LocalDate, String[]> entriesHoursPerDay;
	
	private Predicate<LocalDate> isWeekday = 
			date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;
	
	private Predicate<LocalDate> isNotHoliday =
			date -> holidays.stream().filter(holiday -> holiday.isEqual(date)).findFirst().isEmpty();
	
	
	public Timesheet(YearMonth yearMonth, int totalExtraHours, List<LocalDate> holidays) { 
		this.totalExtraHours = totalExtraHours; 
		this.holidays = holidays;
		this.startDate = yearMonth.atDay(1);
		this.endDate = yearMonth.atEndOfMonth();
	}
	
	public Timesheet(YearMonth yearMonth, Integer totalExtraHours) { 
		this(yearMonth, totalExtraHours, new ArrayList<>());
	}
	
	
	public Map<LocalDate, String[]> computeHoursEntriesPerDay() {
		computeHoursPerDay();
		entriesHoursPerDay = hoursPerDay.keySet().stream()
			.collect(Collectors.toMap(date -> date, 
					                  date -> {
					                	 int hours = hoursPerDay.get(date);
					                	 if (hours == HOURS_PER_DAY) return new String[]{getRandomMinuteForHour(12), getRandomMinuteForHour(19)};
					                	 String[] extraHoursEntries = {null, getRandomMinuteForHour(12), getRandomMinuteForHour(13), getRandomMinuteForHour(19)};
					                	 int extraHours = hours - HOURS_PER_DAY;
					                	 int lunchTime = 1, complHour = 1;
					                	 int firstEntry = 12 - extraHours - lunchTime - complHour;
					                	 extraHoursEntries[0] = String.valueOf(getRandomMinuteForHour(firstEntry));
					                	 return extraHoursEntries;
					                  })
					);
		return entriesHoursPerDay;
	}
	
	private Map<LocalDate, Integer> computeHoursPerDay() {
		hoursPerDay = computeStandardHoursPerDay();
		if (totalExtraHours == 0) return hoursPerDay;
		
		// default extra hour distribution
		hoursPerDay.replaceAll( (date, currValue) -> {
				int extraHours = totalExtraHours >= DEFAULT_EXTRA_HOURS_PER_DAY ? DEFAULT_EXTRA_HOURS_PER_DAY : totalExtraHours;
				totalExtraHours -= extraHours;
				return currValue + extraHours;				
			});
		if (totalExtraHours == 0) return hoursPerDay;
		
		// distribute remaining extra hours in extra hours days
		hoursPerDay.replaceAll( (date, currValue) -> {
			boolean isRegularHourDay = currValue == HOURS_PER_DAY;
			if (isRegularHourDay) return currValue;
			int extraHours = totalExtraHours > 0 ? 1 : 0;
			totalExtraHours -= extraHours;
			return currValue + extraHours;				
		});
		if (totalExtraHours == 0) return hoursPerDay;
		
		// distribute remaining extra hours in regular hours days
		hoursPerDay.replaceAll( (date, currValue) -> {
			boolean isRegularHourDay = currValue == HOURS_PER_DAY;
			if (!isRegularHourDay) return currValue;
			int extraHours = totalExtraHours > 0 ? 1 : 0;
			totalExtraHours -= extraHours;
			return currValue + extraHours;				
		});
		return hoursPerDay;	
	}
	
	public void printValues() {
		hoursPerDay.forEach( (date, h) -> {
			int hours = h == HOURS_PER_DAY ? 7 : h;
			int extras = h == HOURS_PER_DAY ? 0 : (h - HOURS_PER_DAY);
			String txt = String.format("%s - %dhs (%d extras) - %s", 
					DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date), hours, extras, Arrays.toString(entriesHoursPerDay.get(date)));
			System.out.println(txt); 
		});
	}
	
	private Map<LocalDate, Integer> computeStandardHoursPerDay() {
		Map<LocalDate, Integer> standardHoursPerDay = startDate.datesUntil(endDate.plusDays(1))
			.filter(isWeekday)
			.filter(isNotHoliday)
			.collect(Collectors.toMap(date -> date, date -> HOURS_PER_DAY));
		return standardHoursPerDay;
	}
	
	private String getRandomMinuteForHour(int hour) {
		int minute = new Random().nextInt(7);
		return DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.of(hour, minute));
	}
	
}
