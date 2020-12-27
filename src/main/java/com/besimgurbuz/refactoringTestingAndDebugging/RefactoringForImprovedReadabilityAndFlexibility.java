package com.besimgurbuz.refactoringTestingAndDebugging;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RefactoringForImprovedReadabilityAndFlexibility {
    /*
    From the start of this book, we've argued that lambda expressions let you write more concise and flexible code.
    The code is more concise because lambda expressions let you represent a piece of behavior in a more compact form
    compared with using anonymous classes. We also showed you in chapter 3 that method references let you write even
    more concise code when all you want to do is pass an existing method as an argument to another method.

    Your code is more flexible because lambda expressions encourage the style of behavior parameterization that we
    introduced in chapter 2. Your code can use and execute multiple behaviors passed as arguments to cope with
    requirement changes.

    In this section, we bring everything together and show you simple steps for refactoring code to gain readability
    and flexibility using the features you learned in previous chapters: lambdas, method references, and streams.
     */

    // Improving code readability
    public static void main(String[] args) {
       ImprovingCodeReadability.main(args);
    }
}
