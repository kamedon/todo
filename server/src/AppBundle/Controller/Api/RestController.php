<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 2/29/16
 * Time: 9:16 AM
 */

namespace AppBundle\Controller\Api;


use FOS\RestBundle\Controller\FOSRestController;

class RestController extends FOSRestController
{

    protected function auth(){
        $authService = $this->get("auth_rest_service");
        $authService->auth();
    }

}