<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 2/29/16
 * Time: 11:24 AM
 */

namespace AppBundle\Controller\Api;


use Nelmio\ApiDocBundle\Annotation\ApiDoc;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Validator\Constraints\UuidValidator;

class TaskApiController extends RestController
{

    /**
     * @ApiDoc(
     *     description="タスク登録",
     *     statusCodes={
     *         200="created task",
     *         403="Header:X-User-Agent-Authorizationの認証失敗 または　apiKeyの認証失敗"
     *     }
     * )
     * @param Request $request
     * @return array
     */
    public function postTasksAction(Request $request)
    {
        $user = $this->authUser();

    }

    /**
     * @ApiDoc(
     *     description="タスク一覧",
     *     statusCodes={
     *         200="list task",
     *         403="Header:X-User-Agent-Authorizationの認証失敗 または　apiKeyの認証失敗"
     *     }
     * )
     * @param Request $request
     * @return array
     */
    public function getTasksAction(Request $request)
    {
        $this->authUser();
        return [];
    }

}