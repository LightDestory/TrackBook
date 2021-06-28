import {Response} from "express";
import Knex from "knex";
import db from "../db/database";
import {Sender} from "../utils/Sender";

function registerUser(email: string, password: string, pen_name: string, response: Response): void{
    // @ts-ignore
    let con: Knex = db.getConnection();
    con.select()
        .from("users")
        .where("email", email)
        .then((rows: any[]) => {
            if (rows.length === 0) {
                con('users')
                    .insert({'email': email, 'pen_name': pen_name, 'password': password})
                    .then(() => {
                        Sender.getInstance().sendResult(response, 201, "User has been registered!")
                    });
            } else {
                Sender.getInstance().sendError(response, Sender.ERROR_TYPE_EXISTING_USER)
            }
        });
}

function loginUser(email: string, password: string, response: Response): void {
    // @ts-ignore
    let con: Knex = db.getConnection();
    con.select('id', 'pen_name', "password")
        .from("users")
        .where("password", password)
        .where("email", email)
        .then((users: any[]) => {
            if (users.length === 0) {
                Sender.getInstance().sendError(response, Sender.ERROR_TYPE_INVALID_PASSWORD)
            } else {
                Sender.getInstance().sendResult(response, 200, {"penName": users[0].pen_name, "token": `${users[0].id}-${users[0].password}`});
            }
        });
}

function changePassword(email: string, pen_name: string, new_password: string, response: Response): void {
    // @ts-ignore
    let con: Knex = db.getConnection();
    con.select()
        .from("users")
        .where("email", email)
        .where('pen_name', pen_name)
        .then((rows: any[]) => {
            if (rows.length === 0) {
                Sender.getInstance().sendError(response, Sender.ERROR_TYPE_USER_NOT_FOUND);
            } else {
                con("users")
                    .update("password", new_password)
                    .where("id", rows[0].id)
                    .then(() => {
                        Sender.getInstance().sendResult(response, 200, "Password has been changed")
                });
            }
        });
}

export {registerUser, loginUser, changePassword}