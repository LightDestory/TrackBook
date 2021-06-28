import {Response} from "express";

export class Sender {

    static ERROR_TYPE_PARAMETER: number = 1
    static ERROR_TYPE_EXISTING_USER: number = 2;
    static ERROR_TYPE_INVALID_PASSWORD: number = 3;
    static ERROR_TYPE_USER_NOT_FOUND: number = 4;
    static ERROR_TYPE_UNAUTH = 5;
    static ERROR_TYPE_UNKNOWNBOOK = 6;
    private static instance: Sender | undefined;

    private constructor() {}

    public static getInstance() {
        if(!Sender.instance){
            Sender.instance = new Sender();
        }
        return Sender.instance;
    }

    public sendError(response: Response, errorType: number) {
        let status: number;
        let error: any = {"status": "error", "result": ""};
        switch (errorType) {
            case Sender.ERROR_TYPE_EXISTING_USER:
                status = 400;
                error.result = "The email is already linked to a user!"
                break;
            case Sender.ERROR_TYPE_INVALID_PASSWORD:
                status = 401;
                error.result = "The entered password is invalid"
                break;
            case Sender.ERROR_TYPE_USER_NOT_FOUND:
                status = 404;
                error.result = "No user has been found with the entered credentials"
                break;
            case Sender.ERROR_TYPE_UNAUTH:
                status = 403;
                error.result = "The authorization token is invalid"
                break;
            case Sender.ERROR_TYPE_UNKNOWNBOOK:
                status = 404;
                error.result = "I don't know this book!"
                break;
            default /*ERROR_TYPE_PARAMETER*/:
                status = 400;
                error.result = "Invalid request's parameters!";
                break;
        }
        response.status(status).json(error);
    }

    public sendResult(response: Response, httpCode: number, result: any){
        let data: any = {"status": "success", "result": ""};
        data.result = result;
        response.status(httpCode).json(data);
    }
}