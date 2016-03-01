<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 2/29/16
 * Time: 8:59 AM
 */

namespace AppBundle\Controller\Api;


use AppBundle\Entity\ApiKey;
use FOS\RestBundle\Controller\FOSRestController;
use Ramsey\Uuid\Uuid;
use Symfony\Component\HttpFoundation\Request;
use Nelmio\ApiDocBundle\Annotation\ApiDoc;

class AuthApiController extends RestController
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
    public function postLoginAction(Request $request)
    {
        $this->auth();
        $user = $this->get("fos_user.user_manager")->findUserByEmail($request->get("user"));
        if (!$user) {
            return ["code" => 403, "message" => "not found user"];
        }
        $factory = $this->get('security.encoder_factory');


        $encoder = $factory->getEncoder($user);

        $isValidPassword = ($encoder->isPasswordValid($user->getPassword(), $request->get("password"), $user->getSalt())) ? true : false;
        if (!$isValidPassword) {
            return ["code" => 403, "message" => "not found user"];
        }

        $keyApi = $this->getDoctrine()->getRepository("AppBundle:ApiKey")->findBy(["user" => $user]);
        if(!$keyApi){
            $token= Uuid::uuid1()->toString();
            $keyApi = new ApiKey();
            $keyApi->setUser($user);
            $keyApi->setToken($token);
            $manager = $this->getDoctrine()->getManager();
            $manager->persist($keyApi);
            $manager->flush();
        }

        return [
            'user' => [
                'id' => $user->getId(),
                'username' => $user->getUsername()
            ],
            'api_key' => ["token" => $keyApi->getToken()],
            'message' => "created new user"
        ];
    }


}