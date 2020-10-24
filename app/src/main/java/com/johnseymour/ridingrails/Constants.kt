package com.johnseymour.ridingrails

import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Constants
{
     object Formatters
     {
         val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

         val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

         val APIDateFormatter: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE
     }
}