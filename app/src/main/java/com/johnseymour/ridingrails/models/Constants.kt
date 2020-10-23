package com.johnseymour.ridingrails.models

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Constants
{
     object Formatters
     {
         val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).apply {
             withZone(ZoneId.systemDefault())
         }

         val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).apply {
             withZone(ZoneId.systemDefault())
         }
     }
}