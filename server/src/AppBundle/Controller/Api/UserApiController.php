<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 2/29/16
 * Time: 9:14 AM
 */

namespace AppBundle\Controller\Api;


use FOS\RestBundle\Controller\FOSRestController;
use Symfony\Component\HttpFoundation\Request;
use Nelmio\ApiDocBundle\Annotation\ApiDoc;

class UserApiController extends RestController
{
    /**
     * @ApiDoc(
     *     description="ユーザ登録",
     *     statusCodes={
     *         200="Returned create token",
     *         403="Header:X-User-Agent-Authorizationの認証失敗"
     *     }
     * )
     * @param Request $request
     * @return array
     */
    public function postUsersAction(Request $request)
    {
        $this->auth();
    }



}