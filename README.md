# discord-commands-starter
Spring boot command wrapper for JDA

## Setup

maven:

```xml
<dependency>
  <groupId>io.github.sepgh</groupId>
  <artifactId>discord-commands-starter</artifactId>
  <version>0.1.3</version>
</dependency>
```

## Usage

To register commands, first create a class and annotate it with `@DiscordController`. This annotation will be scanned by Spring Boot and will be treated as a bean, so you can `Autowire` any other beans and treat it like `@Component` or `@Service`.


#### `@DiscordCommand`

Use this annotation above your `DiscordController` methods to register them as commands.

- Name: name of the command. Should be alphanumeric, but `slash commands` have their own limitations such as being lowercase.
- Description (optional): description of the command
- Scope (optional): determines the scope of the command. It can be `SERVER` for guild scope or `USER` for application scope.
- Type (optional): determines if the command should be registered as a `SLASH` command or `BASIC`.
- slashDiffer (optional) (default=true): determines if `differ` should be sent automatically when slash command is executed

#### `@DiscordParameter`

Use this annotation behind your `DiscordCommand` method parameters.

- Name: parameter name
- Description (optional): description of the parameter
- Value (optional): default value for the parameter (doesn't work in `SLASH` command type)
- Required (optional) (default=true): determines if the parameter is required

### Configuration

Example `application.yaml`:

```yml
spring:
  discord:
    enabled: true
    token: ''
    basic-command-signature: '!'
```

### Example:

```java
@DiscordCommand(name="register", scope = DiscordCommand.Scope.USER)
public void register(@DiscordParameter(name = "name", description = "Enter your name") String name){
    System.out.println(name);
    // This is how you can get the event from discord
    SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
    event.getHook().sendMessage("You are registered :)").queue();
}
```

Or a basic command:

```java
@DiscordCommand(name="register", scope = DiscordCommand.Scope.SERVER, type = DiscordCommand.Type.BASIC)
public void register(@DiscordParameter(name = "name", description = "Enter your name") String name){
    GuildMessageReceivedEvent event = CommandContextHolder.getContext().getGuildMessageReceivedEvent().get();
    event.getMessage().reply("You are registered :)").queue();
}
```

---

To run a basic command in Discord, try:

```
!register john
```

for longer strings try:

```
!register name="john smith"
```

or

```
!register "john smith"
```
