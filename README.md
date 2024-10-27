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

Create an instance of the `ComposeStore` from a regular Store instance using the `composeStore()` function.
For example, if you have a Store in your ViewModel, it would look like this:

```
@AndroidEntryPoint
class YourActivity : ComponentActivity() {
    private val yourViewModel: YourViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // create ComposeStore instance
            val store = composeStore(YourViewModel.store)
        
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
Specify the target state using generics.

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

State properties can be accessed with `this`.

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
Specify the target state using generics.

```
store.handle<YourEvent.ShowToast> {
    // do something..
}
```

Event properties can be accessed with `this`.

You can also subscribe to parent types.

```
store.handle<YourEvent> {
    when (this) {
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

#### preview on IDE

Use `previewComposeStore()` function.
Specify the target state using the method argument.

```
@Preview
@Composable
fun SomePreview() {
    MyApplicationTheme {
        YourChildComposableComponent(
            store = previewComposeStore(
                state = YourState.Stable,
            ),
        )
    }
}
```

## Middleware

TODO
