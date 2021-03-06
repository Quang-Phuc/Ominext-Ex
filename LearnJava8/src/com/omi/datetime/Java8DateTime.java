package com.omi.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Java8DateTime {

	public static void main(String[] args) {

		Java8DateTime dateTime = new Java8DateTime();
		dateTime.showDate();
		dateTime.getDay();
		dateTime.PeriodExample();
	}

	/*
	 * Show function view Date
	 */
	public void showDate() {
		// Show day- month now
		LocalDate today = LocalDate.now();
		common.print(today);
		// Khoi tao ngay thang cu the
		LocalDate localDate1110 = LocalDate.of(2101, Month.MAY, 10);
		common.print(localDate1110);
		// Khoi tao ngay thu 30 cua nam
		LocalDate AferDay = LocalDate.ofYearDay(2019, 100);
		common.print(AferDay);
	}

	/*
	 * Show function view Time
	 */
	public void showTime() {
		// Show time
		LocalTime todayTime = LocalTime.now();
		
		common.print(todayTime);
		// Set time at 11h30p30s
		LocalTime afterday = LocalTime.of(11, 30, 30);
		
		common.print(afterday);
		// view s thu n cua 1 ngay
		LocalTime sencondsOfDay = LocalTime.ofSecondOfDay(555);
	
		common.print(sencondsOfDay);
	}

	public void getDay() {
		LocalDate date = LocalDate.of(2019, 10, 4);
		boolean isBefore = LocalDate.now().isBefore(date);
		if (isBefore) {
		
			common.print("Now < date");
		} else {
		
			common.print("Now > date");
		}
		Month nameMonth = date.getMonth();
		// get month
		common.print(nameMonth);
		// 2014-04-01 10:45
		LocalDateTime dateTime = LocalDateTime.of(2014, Month.APRIL, 1, 10, 45);
		// Dinh dang theo ISO date (20140220)
		String asBasicIsoDate = dateTime.format(DateTimeFormatter.BASIC_ISO_DATE);
		common.print(asBasicIsoDate);
		// parsing date strings
		LocalDate fromIsoDate = LocalDate.parse("2014-01-20");
		common.print(fromIsoDate);
		LocalDate fromCustomPattern = LocalDate.parse("20.01.2014", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		common.print(fromCustomPattern);
	}
	/*
	 * used Period so sanh date
	 */
	public void PeriodExample() {
		LocalDate firstDate = LocalDate.now();
		LocalDate secondDate = LocalDate.of(2019, 5, 29);
		common.print("firstDate: " + firstDate);
		common.print("secondDate: " + secondDate);
		Period period = Period.between(firstDate, secondDate);
		common.print("period: " + period);
		int days = period.getDays();
		int months = period.getMonths();
		int years = period.getYears();
		
		common.print("Day :" + days + " Month :" + months + " Years :" + years);
	}
	public void InstantExample() {

		Instant instant = Instant.now();
		common.print(instant);
	}

}
