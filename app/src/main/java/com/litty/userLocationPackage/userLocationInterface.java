package com.litty.userLocationPackage;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

// Declaration for lambda functions. Function names declared
// in this interface must match the function names in AWS - JL
public interface userLocationInterface {
    @LambdaFunction
    String updateUserLocation(userLocation userLocationObj);

    @LambdaFunction
    Integer verifyUserLogin(userCredential userCredentialObj);
}
