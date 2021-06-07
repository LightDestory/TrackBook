import { NextFunction, Request, Response } from "express";

export function authCheck(req: Request, res: Response, next: NextFunction) {
    let auth: string|undefined = req.headers.authorization;
    if(!auth){

        res.sendStatus(403);
    }
    else{
        console.log("AUTH OK");
        next();
    }
}