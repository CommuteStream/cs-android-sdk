# CommuteStream Android SDK

This SDK allows you to add CommuteStream ads to your app using MoPub or AdMob for mediation.

These instructions assume you have already followed the instructions at: [https://commutestream.com/sdkinstructions](https://commutestream.com/sdkinstructions).


## Requirements
Your app needs the follow in addition to any sub-requirements of MoPub or AdMob.

- Android 4.0 (API Version 14) and up
- MoPub (for MoPub integration)
- Google Firebase 10.0 or newer (for AdMob integration)*

## Downloading
The following two options are availible for obtaining our SDK:

1. **JCenter AAR (Recommended)**

    The easiest way to add the CommuteStream to your app is to add the following to your *build.gradle* file.

    ```
    repositories {
        jcenter()
    }

    dependencies {
        compile 'com.commutestream.sdk:commutestream-sdk:0.8.0'
    }
    ```

2. **GitHub Repository**

    If desired you may build the SDK locally.

    - Clone the repository `https://github.com/CommuteStream/android-sdk.git`
    - Run `gradle sdk:install` to build the release. This will install the SDK into your local Maven repository.


## Adding the SDK

1. Add `import com.commutestream.sdk.CommuteStream;` to the top of the your class file that initializes AdMob/Mopub. This is typically in your main activity.
2. (Optional) Add the following line of code to so that templated ads more closely to the look of your app. Supported themes are: "light" and "dark". `CommuteStream.setTheme(String theme);`
3. (if using Proguard) When using proguard you will need to add the following rules to your proguard-rules.pro file in order for the SDK to work with AdMob/MoPub mediation. Without these rules you will get a warning in the log from Ads/W saying our AdMob adapter is using an out of date interface.

```
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keep public class com.google.android.gms.ads.** {
public *;
}
-keep public class com.google.ads.** {
public *;
}
-keep public class com.commutestream.sdk.** {
*;
}
-dontwarn okio.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions
```

## Test Banners
Test banners are a great way to make sure things are working as they should be. There are two ways to force test banners to show up in your app. The first method is helpful during development, and the second is useful for diagnoising problems with released apps and/or specific devices.

1. You can invoke test banners programmatically by adding `CommuteStream.setTestingFlag(true);` to your app. Be sure to remove or comment this line before releasing your app in the Play Store. If everything is working you will see a banner with a CS logo.
2. Test banners can also be set on a per-device basis through the CommuteStream web interface. After logging into your CommuteStream publisher account click the "Apps" tab, then select "Test Devices". Review the instructions on the page, then add a new test device by clicking the "New Test Device" button and entering the required infomation.

**Important:** Your AdMob/MoPub mediations settings must be setup correctly in order to see and CS test banners. See [https://commutestream.com/sdkinstructions](https://commutestream.com/sdkinstructions) for more details. Also, be sure Admob/MoPub is not in testing mode itself, as this will prevent CS test banners from showing up.

## Recommended Usage
Our ability to deliver quality and meaningful deals, events, and advertising, hinges on your implementation of the below methods. Hence, we pay you based on your implementation of these methods. In order to receive the maximum revenue possible, you will need to implement all of the following methods (as applicable to your app) and follow the requirements for each.

We start off paying all of our developers the maximum revenue (currently $4 per 1000 impressions). After a few weeks we will conduct a review of the data being provided by your app and adjust your revenue rate based on how closely you follow the SDK requirements. Note that some of these methods may not apply to all apps.

**IMPORTANT:** Be sure to import the CommuteStream SDK into all your class files that will call the below methods. Use route ID, and stop ID as defined by that agency’s GTFS data and use the agency ID from the Agency ID table in the section below. We understand that in some cases the stop ID and route ID may not be applicable, in which case you can set these to null. Please refer to our  [Best Practices doc](https://commutestream.com/bestpractices) for further information.

### Display of Transit Info
The following methods are to be made every time each of the following types of transit information are displayed to the user. These methods should be made multiple times in succession if multiple pieces of information are displayed to the user at the same time. For example if one view shows the arrival times for three different stops, the trackingDisplayed method should be made three times.

#### Arrival Time/Tracking Info:
```
CommuteStream.trackingDisplayed(String agency_id, String route_id, String stop_id);
```

#### Agency Alert Info:
```
CommuteStream.alertDisplayed(String agency_id, String route_id, String stop_id);
```

#### Map Info (Geographic Data):
```
CommuteStream.mapDisplayed(String agency_id, String route_id, String stop_id);
```

### User Actions
The following methods are to be made every time the user peforms one of the following actions:

#### "Bookmarks" or "Favorities" a Transit Stop:
```
CommuteStream.favoriteAdded(String agency_id, String route_id, String stop_id);
```

#### Selects Stop As The Starting Point Of a Trip:
```
CommuteStream.tripPlanningPointA(String agency_id, String route_id, String stop_id);;
```

#### Selects Stop As The Final Destination Of a Trip:
```
CommuteStream.tripPlanningPointB(String agency_id, String route_id, String stop_id);
```

### Location
Previous versions of the CommuteStream SDK required calls to `CommuteStream.setLocation(Location location);` -- this is no longer needed. The SDK now obtains location info automatically in apps that utilize location services. If your app does not use location services, or the user has not granted location permissions, location data will not be collected.

## Agency IDs
The following ids are to be used in the various method calls above where "agency_id" is required. We are adding new markets reguarly; the current list is availible [here](https://commutestream.com/markets).

| Description   | Agency ID / CS ID  |
| ------------- | -----|
| Chicago CTA | cta |
| Chicago Metra | METRA |
| Chicago PACE | PACE |
| Boston MBTA | MBTA |
| Pittsburgh PAAC | PAAC |
| Minneapolis / St. Paul Area | MINNEAPOLIS |
| Washington DC Area | DC |
| Los Angeles County | LA |
| Philadelphia - SEPTA Bus | SEPTABUS |
| Philadelphia - SEPTA Rail | SEPTARAIL |
| Seattle Area (King County Metro) | SEATTLE |
| Miami-Dade County Metro | MIAMI |
| Portland - TriMet | TRIMET |
| Salt Lake City Area - UTA | UTA |
| Utah - Cache Valley Transit District | CVTD |
| NYC - MTA Subway | MTA_SUBWAY |
| NYC - MTA Bus | MTA_BUS |
| NYC - MTA Metro North | MTA_METRO_NORTH |
| NYC - MTA Long Island Railroad | MTA_LONG_ISLAND |
| New Jersey - NJ Transit Rail | NJT_RAIL |
| New Jersey - NJ Transit Bus | NJT_BUS |
| San Diego - Metropolitan Transit System | MTS |
| San Francisco - SFMTA | SFMTA |
| San Francisco - Bay Area Rapid Transit | BART |
| San Francisco - AC Transit | AC |
| Baltimore - Maryland Transit Administration | MARYLAND |
| Las Vegas - RTC | RTC |
| Tampa - Hillsborough Area Regional Transit | HART |
| London - Transport for London | TFL |
| Toronto - Toronto Transit Commission | TTC |


## Logging

Log messages from the CommuteStream SDK are sent to LogCat under the "CS_SDK" tag inside Android Studio.