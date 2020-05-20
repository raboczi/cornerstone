The ZK Theme Template serves as a base theme, allowing developers to make changes and create custom ZK themes. It comes with continuous/incremental compile and live-reload features to minimize the turn-around time when developing a theme.

# Build Steps
## Building prerequisites
Require Node.js \>= 10.16

## clone
clone the zkThemeTemplate project

## initially
```
./init.sh
npm install
```

## build jar file
`mvn clean package`

The jar file will be `target/circadian.jar`

# Development

## switch to compact profile (since 9.1.0)
1. Open `src/archive/web/zul/less/_zkvariables.less`
2. Modify `@themeProfile` to "compact".

``` less
@themeProfile:                 "default";
@themePalette:                 "iceblue";
```

3. now the theme uses the compact profile.

## compile run preview app
`mvn test exec:java@preview-app`

## preview
open a simple preview page in the browser, add your own pages containing the components to preview http://localhost:8080

## continuous compile/watch less files
in a separate console:

`npm run zklessc-dev`

## wrap up
update less-files with text editor, save file -\> auto zkless compile -\> browser will reload style sheet zk.wcs

# How to use `circadian.jar`:

1. Put `circadian.jar` in `WEB-INF/lib`, then `circadian.jar`
    will become your default theme if there is no other theme.

2. Now you can also dynamically switch between different themes by
    cookie or library property
  -  Use library-property
     ```
        <!-- in WEB-INF/zk.xml -->
        <library-property> 
            <name>org.zkoss.theme.preferred</name>
            <value>circadian</value>
        </library-property> 
     ```


  - Use cookie to switch theme, add a cookie
    ```
    zktheme=circadian
    ```
It does not require a server restart, but user has to refresh the browser.
