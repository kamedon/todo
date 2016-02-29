<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 2/29/16
 * Time: 9:14 AM
 */

namespace AppBundle\Controller\Api;


use AppBundle\Entity\ApiKey;
use AppBundle\Entity\NewUserRequest;
use AppBundle\Form\NewUserFormType;
use AppBundle\Form\UserType;
use Symfony\Component\Config\Definition\Exception\Exception;
use Symfony\Component\HttpFoundation\Request;
use Nelmio\ApiDocBundle\Annotation\ApiDoc;
use Symfony\Component\HttpKernel\Exception\HttpException;

class UserApiController extends RestController
{
    /**
     * @ApiDoc(
     *     description="ユーザ登録",
     *     statusCodes={
     *         200="Returned create token",
     *         403="Header:X-User-Agent-Authorizationの認証失敗",
     *         400="登録ユーザの情報が間違っている"
     *     }
     * )
     * @param Request $request
     * @return array
     */
    public function postUsersAction(Request $request)
    {
        $this->auth();

        $userManager = $this->get('fos_user.user_manager');
        $user = $userManager->createUser();
        $user->setEnabled(true);

        $u = new NewUserRequest();
        $form = $this->get('form.factory')->createNamed('', NewUserFormType::class, $u, [
            'method' => 'POST',
            'csrf_protection' => false,
        ]);

        $form->handleRequest($request);
        if ($form->isValid()) {
            $user->setEmail($u->getEmail());
            $user->setEmailCanonical($u->getEmail());
            $user->setPlainPassword($u->getPlainPassword());
            $user->setUsername($u->getUsername());

            if ($userManager->findUserByUsername($user->getUsername()) || $userManager->findUserByEmail($user->getEmail())) {
                throw new HttpException(400, "not unique user");
            }

            try {
                $userManager->updateUser($user);
            } catch (Exception $e) {
                throw new HttpException(400, "New User is not valid.");
            }
            $key = \Ramsey\Uuid\Uuid::uuid1()->toString();
            $apiKey = new ApiKey();
            $apiKey->setUser($user);
            $apiKey->setToken($key);
            $em = $this->getDoctrine()->getManager();
            $em->persist($apiKey);
            $em->flush();

            return [
                'user' => [
                    'id' => $user->getId(),
                    'username' => $user->getUsername()
                ],
                'api_key' => ["token" => $key],
                'message' => "created new user"
            ];
        }
        throw new HttpException(400, "New User is not valid.");

    }


}