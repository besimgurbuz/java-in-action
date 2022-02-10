## Summary

- Lambda expressions can make your code more readable and flexible.
- Consider converting anonymous classes to lambda expressions, but be wary of subtle semantic differences such as the meaning of the keyword *this* and shadowing of variables.
- Method references can mek your code more readable compared with lambda expressions.
- Consider converting iterative collection processing to use the Streams API.
- Lambda expressions can remove boilerplate code associated with several object-oriented design patterns, such as strategy template method, observer, chain of responsibility, and factory.
- Lambda expression can be unit-tested, but in general, you should focus on testing the behavior of the methods in which the lambda expressions appear.
- Consider extracting complex lambda expressions into regular methods.
- Lambda expressions can make stack traces less readable.
- The *peek* method of a stream is useful for logging intermediate values as they flow past certain points of a stream pipeline.
