# Tart

Tart is Kotlin Multiplatform MVI Framework.

## Installation

```
implementation("io.github.hkusu.muscat:muscat-core:<latest-version>")

// or, in libs.versions.toml
tart-core = { module = "io.github.hkusu.muscat:muscat-core", version = "<latest-version>" }
```

## Usage

TODO

## Compose

You can use `.state`, `.event`, `.dispatch()` provided by Store, but we have a class for Compose.

### Installation

```
implementation("io.github.hkusu.muscat:muscat-compose:<latest-version>")

// or, in libs.versions.toml
tart-compose = { module = "io.github.hkusu.muscat:muscat-compose", version = "<latest-version>" }
```

### Usage

#### Create ComposeStore instance

Create an instance of the `ComposeStore` from a regular Store instance using the `ComposeStore#create()` function.
For example, if you have a Store in your ViewModel, it would look like this:

```
@AndroidEntryPoint
class YourActivity : ComponentActivity() {
    private val yourViewModel: YourViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // create ComposeStore instance
            val store = ComposeStore.create(YourViewModel.store)
        
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // pass as an argument to child Composable component
                    YourChildComposableComponent(
                        store = store,
                    )
        // ... 
```

#### Rendering according to State

Use `ComposeStore.render()` method.
Specify the target State using generics.

```
store.render<YourState.Stable> {
    YourComposableComponent()
}
```

If it does not match the current State, the `{ }` block will not be executed.
Therefore, you can define views for each State side by side.

```
store.render<YourState.Loading> {
    YourComposableComponent_A()
}

store.render<YourState.Stable> {
    YourComposableComponent_B()
}
```

State properties can be accessed with `this` scope.

```
store.render<YourState.Stable> {
    YourComposableComponent(url = this.url) // this. can be omitted
}
```

#### Dispatch Action

Use `ComposeStore.dispatch()` method.

```
Button(
    onClick = { store.dispatch(YourAction.ClickButton) },
) {
// ...
```

#### Event handling

Use `ComposeStore.handle()` method.
Specify the target State using generics.

```
store.handle<YourEvent.ShowToast> { event ->
    // do something..
}
```

Event properties can be accessed with `this` scope.

You can also subscribe to parent types.

```
store.handle<YourEvent> { event ->
    when (event) {
        is YourEvent.ShowToast -> {
          // do something..
        }
        is YourEvent.GoBack -> {
          // do something..
        }
        // ...
    }
}
```

#### Preview on IDE

Use `ComposeStore#create()` function with target State.

```
@Preview
@Composable
fun SomePreview() {
    MyApplicationTheme {
        YourChildComposableComponent(
            store = ComposeStore.createMock(
                state = YourState.Stable,
            ),
        )
    }
}
```

## Middleware

You can create functions linked to State etc. on the Store.
To do this, create a class with the `Middleware` interface and override the necessary methods.

```
class YourMiddleware<S : State, A : Action, E : Event> : Middleware<S, A, E> {
    override suspend fun runAfterStateChange(state: S, prevState: S) {
        // do something..
    }
}
```

The `:tart-logging` module in this repository is an example of Middleware that does simple logging.

### Apply Middleware

TODO
