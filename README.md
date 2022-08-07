# JS<img width="22" src='https://upload.wikimedia.org/wikipedia/commons/c/c9/JSON_vector_logo.svg'/>Nparser

## Introduction

### Goals
The purpose of this project is learning and fun.

I aimed to recreate some of the functionality of the libraries `GSON` and `Jackson`,
which enables users to parse and transform data supplied in JSON format, and transform it
into Java objects.

### What's working (mostly)

Currently, you can use the `JSON.parse` method to transform a json string into an object representation of
the data, this can be transformed back to a string format, with the `toString()` method of the returned instance.

You can also `convert` this `JSON` object to a model class, that is a POJO,
and represents the content and structure of the JSON string.

You can use the `@JsonPropertyName` annotation, if the property name in the JSON string 
does not match with the field name of the Java object.

### Todo
- Converting a model instance back to JSON representation.
- Implementing the processing of escape characters.

## Warning

I do not recommend to use it in production.

## Technologies
 - java 18
 - maven 3.8.6

## License
![Dr. Evil: I demand the sum of... ONE MILLION DOLLARS!](http://www.quickmeme.com/img/12/12fc8f7415061141fc2a79464b7847602475430c05385d5e731e1994b12881c0.jpg)