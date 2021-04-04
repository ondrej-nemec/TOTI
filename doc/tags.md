# Available tags

## Comment

Comment part of your code, so TOTI will ignore it.

```
<%-- comment here --%>
```

## Variables

### Define new variable

Create new variable with given type, name and optionally value. Unpaired

```
<t:var type="" name="" value="" final />
```
`value` and `final` are optional.

### Set value

Set value to defined variable. Unpaired

```
<t:set name="" value="" />
```

### Print on page

Print variable to HTML page. With `nonescape` parameter, value will not be escaped. Unpaired

```
<t:out name="" />
<t:out name="" nonescape />
```

### Print to console

Print variable/text to system console. Unpaired

```
<t:console value="" />
<t:console text="" />
```

## Condition

### Switch

Java *switch*. Paired

```
<t:switch object=""></t:switch>
```

### Case

Java switch *case*. Always between start and closing switch tag. Unpaired

```
<t:case cond="" />
```

### Default

Java *default* switch state. Always after start switch tag or case tag and before closing switch tag. Unpaired

```
<t:default />
```

### If

Java *if* statement. Paired

```
<t:if cond=""></t:if>
```

### Else if

Java *else-if* statement. Always between start and closing if tag. Unpaired

```
<t:elseif cond="" />
```

### Else

Java *else* statement. Always after start if tag or else-if tag and before closing if tag. Unpaired

```
<t:else />
```

## Cycles

### For

Java *for* cycle. Paired

```
<t:for from="" to="" change=""></t:for>
```

### Foreach

Java *foreach* cycle. Allow any Iterable include primitive array or Map. Paired

```
<t:foreach item="" collection=""></t:foreach>
<t:foreach key="" value="" map=""></t:foreach>
```

### While

Java *while* cycle. Paired

```
<t:while cond=""></t:while>
```

### Do-while

Java *do-while* cycle. Paired

```
<t:dowhile cond=""></t:dowhile>
```

## Break, continue

### Continue

Java *continue* keyword. Unpaired

```
<t:continue />
```

### Break

Java *break* keyword. Unpaired

```
<t:break />
```

## Try-catch-finally

### Try

Java *try* statement. Pair

```
<t:try></t:try>
```

### Catch

Java *catch* statement. Always between start and closing try tag. Unpaired

```
<t:catch exception="" name="" />
```
`exception` parameter is optional. If not used, java.lang.Exception is used.

### Finally

Java *finally* statement. Always after start try tag or catch tag and before closing try tag. Unpaired

```
<t:finally />
```

## Translate

### Translate

Translate text/value of variable with given Translator instance.<br>
Unpaired: translate single message
Paired: translate with parameters - see translate parameters

```
<t:trans message="" />
<t:trans message=""></t:trans>
<t:trans variable="" />
<t:trans variable=""></t:trans>
```

### Translate parameter

Parameters for translation message. Always between start and closing trans tag. Unpaired

```
<t:param key="" value="" />
```

## Templating

### Layout

Select another template as parent. Can be only one on one page. Optionally can be used template from another module. Unpaired

```
<t:layout path="" />
<t:layout path="" module="" />
```

### Block

Define block for used in future. Pair

```
<t:block name=""></t:block>
```

### Include

Include defined block/another template file on given place. Template can be included from another module. Block can be signed as optional, otherwise exception occur if missing. Unpaired

```
<t:include block="" />
<t:include block="" optional/>
<t:include file="" />
<t:include file="" module="" />
```

## Control

### Control

Show grid/form object. For grid always unpaired. For form unpaired for default view, paired for custom view (see Error, Label and Input). Name is grid/form variable name.
Can be used in JS, with 'jsObject' returns new configured TotiForm/TotiGrid object.

```
<t:control name="" />
<t:control name="" jsObject/>
```

### Error, Label, Input

Show form element. Unpaired.

```
<t:error name="" />
<t:label name="" />
<t:input name="" />
```