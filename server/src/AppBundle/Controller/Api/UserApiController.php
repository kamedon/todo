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
                return ["code" => 400, "errors" => ["other" => ["errors" => "It has already been registered"]], "message" => "It has already been registered"];
            }

            try {
                $userManager->updateUser($user);
            } catch (Exception $e) {
            }
            $key = \Ramsey\Uuid\Uuid::uuid1()->toString();
            $apiKey = new ApiKey();
            $apiKey->setUser($user);
            $apiKey->setToken($key);
            $em = $this->getDoctrine()->getManager();
            $em->persist($apiKey);
            $em->flush();

            return [
                'code' => 201,
                'user' => [
                    'id' => $user->getId(),
                    'username' => $user->getUsername(),
                    'email' => $user->getEmail()
                ],
                'api_key' => ["token" => $apiKey->getToken()],
                'message' => "created new user"
            ];
        }
        $errors = $form->getErrors(true, false);

        $errorMessage = [];
        foreach (["email", "username", "plainPassword"] as $key) {
            $e = $errors->getForm()[$key]->getErrors();
            if ($e->count() > 0) {
                $errorMessage[$key] = $e->getForm();
            } else {
                $errorMessage[$key]["errors"] = [];
            }
        }
        return ["code" => 400, "errors" => $errorMessage, "message" => "invalid query"];
    }

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
    public function postUsersLoginAction(Request $request)
    {
        $this->auth();
        $user = $this->get("fos_user.user_manager")->findUserByUsernameOrEmail($request->get("user"));
        if (!$user) {
            return ["code" => 400, "errors" => ["other" => ["errors" => ["It has already been registered"]], "message" => "not found user"]];
        }
        $factory = $this->get('security.encoder_factory');
        $encoder = $factory->getEncoder($user);

        $isValidPassword = ($encoder->isPasswordValid($user->getPassword(), $request->get("password"), $user->getSalt())) ? true : false;
        if (!$isValidPassword) {
            return ["code" => 400, "errors" => ["other" => ["errors" => ["It has already been registered"]], "message" => "not found user"]];
        }

        $keyApi = $this->getDoctrine()->getRepository("AppBundle:ApiKey")->findOneBy(["user" => $user]);
        if (!$keyApi) {
            $key = \Ramsey\Uuid\Uuid::uuid1()->toString();
            $keyApi = new ApiKey();
            $keyApi->setUser($user);
            $keyApi->setToken($key);
            $manager = $this->getDoctrine()->getManager();
            $manager->persist($keyApi);
            $manager->flush();
        }
        return [
            'code' => 200,
            'user' => [
                'id' => $user->getId(),
                'username' => $user->getUsername(),
                'email' => $user->getEmail()
            ],
            'api_key' => ["token" => $keyApi->getToken()],
            'message' => "login success"
        ];
    }


}
