package pt.ulisboa.tecnico.meic.sec.lib;

import com.google.gson.Gson;
import okhttp3.*;
import pt.ulisboa.tecnico.meic.sec.lib.exception.EntityAlreadyExistsException;
import pt.ulisboa.tecnico.meic.sec.lib.exception.EntityNotFoundException;

import java.io.IOException;

public class Node {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String DEFAULT_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 3001;
    private static final String PUT_USER_ENDPOINT = "/";
    private static final String PUT_PASSWORD_ENDPOINT = "/password";
    private static final String RETRIEVE_PASSWORD_ENDPOINT = "/retrievePassword";
    private static final String PUT_IV_ENDPOINT = "/IV";
    private static final String RETRIEVE_IV_ENDPOINT = "/retrieveIV";

    private OkHttpClient client;
    private String address;
    private int port;
    private Gson json;

    public Node(){
        this(DEFAULT_ADDRESS, DEFAULT_PORT);
    }

    public Node(int port){
        this(DEFAULT_ADDRESS, port);
    }

    public Node(String address, int port){
        this.address = address;
        this.port = port;
        this.client = new OkHttpClient();
        this.json = new Gson();
    }

    /**
     * Register user in the server
     *
     * @param user - User
     * @return User - null when user is not successful registered
     * @throws IOException - when remote server node fails to respond
     * @throws EntityAlreadyExistsException - when user already exists in server node
     */
    public User register(User user) throws IOException, EntityAlreadyExistsException {
        RequestBody body = RequestBody.create(JSON, json.toJson(user));
        Request request = new Request.Builder()
                .url(getAPIURL() + PUT_USER_ENDPOINT)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return json.fromJson(response.body().string(), User.class);
        } else {
            return null;
        }
    }

    /**
     * Create a new password in server
     *
     * @param pwd - Password
     * @return Password
     * @throws IOException - when remote server fails to respond
     * @throws EntityAlreadyExistsException - when password already exists in server node
     */
    public Password putPassword(Password pwd) throws IOException, EntityAlreadyExistsException {
        RequestBody body = RequestBody.create(JSON, json.toJson(pwd));
        Request request = new Request.Builder()
                .url(getAPIURL() + PUT_PASSWORD_ENDPOINT)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return json.fromJson(response.body().string(), Password.class);
        } else {
            if(response.code() == SecureEntity.ALREADY_EXISTS_CODE)
                throw new EntityAlreadyExistsException(json.fromJson(response.body().string(), Password.class));
            return null;
        }
    }

    /**
     * Retrieve a password from server
     *
     * @param pwd - Password
     * @return Password
     * @throws IOException - when remote server fails to respond
     * @throws EntityNotFoundException - when password does not exist in server node
     */
    public Password retrievePassword(Password pwd) throws IOException, EntityNotFoundException {
        String input = json.toJson(pwd);

        RequestBody body = RequestBody.create(JSON, input);
        Request request = new Request.Builder()
                .url(getAPIURL() + RETRIEVE_PASSWORD_ENDPOINT)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return json.fromJson(response.body().string(), Password.class);
        } else {
            if(response.code() == SecureEntity.NOT_FOUND_CODE)
                throw new EntityNotFoundException();
            return null;
        }
    }

    /**
     * Create new IV in server, or update if already exists
     *
     * @param iv - IV
     * @return IV
     * @throws IOException - when remote server fails to respond
     */
    public IV putIV(IV iv) throws IOException {
        RequestBody body = RequestBody.create(JSON, json.toJson(iv));
        Request request = new Request.Builder()
                .url(getAPIURL() + PUT_IV_ENDPOINT)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return json.fromJson(response.body().string(), IV.class);
        } else {
            return null;
        }
    }

    /**
     * Retrieve an IV from server
     *
     * @param iv - IV
     * @return IV
     * @throws IOException - when remote server fails to respond
     * @throws EntityNotFoundException - when password does not exist in server node
     */
    public IV retrieveIV(IV iv) throws IOException, EntityNotFoundException {
        String input = json.toJson(iv);

        RequestBody body = RequestBody.create(JSON, input);
        Request request = new Request.Builder()
                .url(getAPIURL() + RETRIEVE_IV_ENDPOINT)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return json.fromJson(response.body().string(), IV.class);
        } else {
            if(response.code() == SecureEntity.NOT_FOUND_CODE)
                throw new EntityNotFoundException();
            return null;
        }
    }

    private String getAPIURL(){
        return "http://" + address + ":" + port;
    }


}
