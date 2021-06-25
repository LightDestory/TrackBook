import express, {Request, Response} from 'express';
import Knex from "knex";
import db from "../db/database"

export const accountRoute = express.Router();

accountRoute.post('/register', (req: Request, res: Response) => {
    let email: string | undefined = req.body.email;
    let password: string | undefined = req.body.password;
    let penName: string | undefined = req.body.penName;
    if (!email || !password || !penName || password.length != 32) {
        res.status(400).json({"status": "error", "code": "1", "message": "Invalid request's parameters!"});
        return;
    }// @ts-ignore
    let con: Knex = db.getConnection();
    con.select()
        .from("users")
        .where("email", email)
        .then((rows: any) => {
            if (rows.length === 0) {
                con('users')
                    .insert({'email': email, 'pen_name': penName, 'password': password})
                    .then(() => {
                        res.status(201).json({"status": "success", "result": "User registered!"});
                    });
            } else {
                res.status(400).json({"status": "error", "code": "2", "result": "Email is registered!"});
            }
        });
});

accountRoute.post('/login', (req: Request, res: Response) => {
    let email: string | undefined = req.body.email;
    let password: string | undefined = req.body.password;
    if (!email || !password) {
        res.status(400).json({"status": "error", "code": "1", "message": "Invalid request's parameters!"});
        return;
    }
    // @ts-ignore
    let con: Knex = db.getConnection();
    con.select('id', 'pen_name', "password")
        .from("users")
        .where("password", password)
        .where("email", email)
        .then((users: any[]) => {
            if (users.length === 0) {
                res.status(401).json({"status": "error", "result": "Invalid Password!"});
            } else {
                res.status(200).json({"status": "success", "result": {"penName": users[0].pen_name, "token": `${users[0].id}-${users[0].password}`}});
            }
        });
});