package com.omi.datetime;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class betweenLocalDateTimeAndOffsetDataTime {
	public static void main(String[] args) {
		betweenLocalDateTimeAndOffsetDataTime Difference = new betweenLocalDateTimeAndOffsetDataTime();
		Difference.Difference();
	}
	public void Difference()
	{
		LocalDateTime dateTimeLocal = LocalDateTime.now();
		OffsetDateTime offsetDateTime = OffsetDateTime.now();
		ZonedDateTime zoneDateTime =ZonedDateTime.now();
		System.out.println("LocalDateTime :"+dateTimeLocal);
		System.out.println("offsetDateTime :"+offsetDateTime);
		System.out.println("zoneDateTime :"+zoneDateTime);
	}
}
