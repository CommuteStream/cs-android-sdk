# CommuteStream Android SDK

An Android library project that provides a simple way to add CommuteStream
as a source of AdMob mediated banner advertising in Android applications.

## Building Release

``` sh
gradle sdk:generateRelease:
```

## Test Application

The project includes in it a test application which may be used to actively generate randomized
transit interest and android device ids while then showing ads. This is useful in showing the SDK
is working as expected in functional testing.

The release .aar file is then in sdk/build/release
