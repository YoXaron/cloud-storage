package dev.yoxaron.cloudstorage.docs;

public class SwaggerExamples {

    //auth
    public static final String AUTH_VALIDATION_ERROR = """
            {
              "message": "Username must contain at least 4 characters"
            }
            """;

    public static final String AUTH_BAD_CREDENTIALS = """
            {
              "message": "Bad credentials"
            }
            """;

    public static final String NOT_AUTHENTICATED = """
            {
              "message": "Not authenticated"
            }
            """;

    public static final String USER_RESPONSE = """
            {
              "username": "user_1"
            }
            """;

    public static final String USER_ALREADY_EXISTS = """
            {
              "message": "Username is already in use"
            }
            """;

    //resources
    public static final String INVALID_PATH = """
            {
              "message": "Path must start with /"
            }
            """;

    public static final String RESOURCE_NOT_FOUND = """
            {
              "message": "Resource not found"
            }
            """;

    public static final String RESOURCE_ALREADY_EXISTS = """
            {
              "message": "Directory already exists"
            }
            """;



    public static final String INTERNAL_ERROR = """
            {
              "message": "Internal server error"
            }
            """;
}
