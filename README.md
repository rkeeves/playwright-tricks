# playwright-tricks

This repository contains basic examples for Playwright usage.

**Each JUnit test class was INTENTIONALLY written on the most basic level.**

I wanted to focus purely on Playwright.
I purposefully tried to shy away from any intricacies, such as:
- [poor man's abstractions](https://refactoring.guru/design-patterns/abstract-factory)
- [insane man's abstractions](https://www.vavr.io/)
- [state machines](https://youtu.be/boBD1qhCQ94)
- [object algebras](https://blog.acolyer.org/2015/11/13/scrap-your-boilerplate-with-object-algebras/)
- cucumber
- di using pico or spring
- spring
- reporting, logging and other garbage generation

Yes, this means lot of code duplication and verbose test code.
BUT this way each test class is one self-contained unit, without crazy DI or abstractions which would complicate things.
Aka the code is basic, does nothing fancy, just demonstrates some tiny things.
 
To keep the examples small, and basic, I decided to not involve any configuration at all.
There are no intricate maven profiles, resource filtered test configuration files,
or other behind-the-scenes mechanisms.

Also, the tests aren't really tests.
Each example is about the **Playwright**, not about actually testing any application.

## About Node and Unicorns

Yep... you could also use Playwright with Node, no strings attached.
There are some tiny - but still mind-blowing - things that you can only do if you use Node.
I didn't cover that, because if you use Node then you are probably already paying the subscription for Cypress.

Seriously though: JS-JS interaction is naturally more seamless than Java-JS.
So you can do interactions between the browser's js and node's js code more easily.

The Java-JS interaction is a bit bothersome, but it has nothing to do with Playwright itself.
Selenium suffers from this too.
Just to give you an example of how painful the transition from browser to Java test code really is, 
take a look at [how BiDi works](https://github.com/SeleniumHQ/selenium/blob/selenium-4.4.0/javascript/cdp-support/mutation-listener.js).

*(Aka you have to use MutationObserver.
When a node is mutated, you further mutate the node by adding it a **magic attribute** with a pseudo-random value so that you can identify it later.
Then you fire a CDP binding called event (binding was injected earlier by a CDP command from Selenium's Java side) with an arbitrary arg.
The main goal is to pass the magic string to Java, so you shove that **magic attribute**'s value into that arbitrary arg (e.g. json object field).
Then CDP notifies Java that the binding was called, 
sends the arg as char sequence to a Java method.
In Java you try to parse that passed in char sequence into a json.
You pick out that **magic attribute**'s value from that json.
Then you find the WebElement by that **magic attribute**.
Then you call the actual Java test code's callback function and provide it the WebElement that you just found.
Of course, as a test writer, you don't directly have to do this, because Selenium hides it under a more [fancy API](https://www.selenium.dev/documentation/webdriver/bidirectional/bidi_api/#mutation-observation).
But - under the hood - this how [rainbow unicorns are born](https://youtu.be/BcvWH7K1LlA).)*