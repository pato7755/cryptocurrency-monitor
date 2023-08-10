
# CryptoTrackr

A Cryptocurrency monitoring app based on [http://coinapi.io](http://coinapi.io), using CLEAN Architecture.
## Features

- Shows users a comprehensive list of cryptocurrencies
- Allows users to set any cryptocurrency as a favourite
- Filter list to show only favourite cryptocurrencies
- Provides a search functionality for users to type some characters in an asset's name. Eg. BT for Bitcoin.
- Allows users to select any cryptocurrency to view more details about it
- On detail screen, users also see current exchange rate between the selected cryptocurrency and EURO.


## Tech Stack

[Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.

[Jetpack Compose](https://developer.android.com/jetpack/compose) - Android’s modern toolkit for building native UI

[Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.

[Hilt](https://dagger.dev/hilt/) - Provides a standard way to incorporate Dagger dependency injection into an Android application.

[Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - Concurrency design pattern that you can use on Android to simplify code that executes asynchronously

[Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - An asynchronous version of a Sequence, a type of collection whose values are lazily produced.

[StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - A state holder observable flow that emits the current and new state updates to its collectors.

[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn"t destroyed on UI changes. It's basically a state holder/manager for UIs.

[Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation) - Simplified and type-safe navigation for Jetpack Compose.

[Room](https://developer.android.com/topic/libraries/architecture/room) - Persistence library that provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite.

[Coil](https://coil-kt.github.io/coil/) - An image loading library for Android applications which is backed by Kotlin Coroutines.

[JUnit](https://junit.org/junit4/) - Simple framework for writing unit tests.

[Google Truth](https://truth.dev/) - A library for performing assertions in tests.
## Installation

- Clone this repository
- Open the project in Android Studio and wait for the Gradle sync to complete
- Create a file called **local.properties** in the root folder of the project
- Head over to [https://www.coinapi.io/market-data-api/pricing](https://www.coinapi.io/market-data-api/pricing) and create an API key.
- Ceate the *local.properties* file that you created, type **API_KEY=** then paste the new API key you created. See the screenshot below...
![](https://i.imgur.com/QSq43Pi.png)
- Now rebuild the project (i.e Build -> Rebuild)

Now, you're all set to run the project on your device or emulator!
## Screenshots

![](https://i.imgur.com/pyUs6ISm.png)
![](https://i.imgur.com/qCCucCXm.png)
![](https://i.imgur.com/6CyfWWPm.png)
![](https://i.imgur.com/obeI6H9l.png)
![](https://i.imgur.com/JltBaW6l.png)
![](https://i.imgur.com/GTupFcTl.png)
## Improvements
Some potential improvements that could be made:
- Use a background worker to update the asset records with their respective icon URLs.
- Internationalization of the app
- CI/CD 

## Support

For support, email pat.essiam701@gmail.com

I am happy to receive feedback or questions. Let me know if you find any bugs too ;)