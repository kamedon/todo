<?php
/**
 * Created by PhpStorm.
 * User: kamedon
 * Date: 2/29/16
 * Time: 11:24 AM
 */

namespace AppBundle\Controller\Api;


use AppBundle\Entity\Task;
use AppBundle\Form\TaskFormType;
use Nelmio\ApiDocBundle\Annotation\ApiDoc;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Exception\HttpException;
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
        $task = new Task();
        $task->setUser($user);
        $form = $this->get('form.factory')->createNamed('', TaskFormType::class, $task, [
            'method' => 'POST',
            'csrf_protection' => false,
        ]);
        $form->handleRequest($request);
        if ($form->isValid()) {
            $manager = $this->getDoctrine()->getManager();
            $manager->persist($task);
            $manager->flush();
            return [
                "task" => [
                    "id" => $task->getId(),
                    "body" => $task->getBody(),
                    "state" => $task->getState(),
                    "createdAt" => $task->getCreatedAt(),
                ],
                "message" => "created task"
            ];
        }
//        return [$form->getErrors()];
        throw new HttpException(400, "invalid task");
    }

    /**
     *
     * @ApiDoc(
     *     description="タスク一覧",
     *     statusCodes={
     *         200="list task",
     *         403="Header:X-User-Agent-Authorizationの認証失敗 または　apiKeyの認証失敗"
     *     }
     * )
     * @param int $page
     * @param Request $request
     * @return array
     */
    public function getTasksAction($page = 1, Request $request)
    {
        $user = $this->authUser();
        $repository = $this->getDoctrine()->getRepository("AppBundle:Task");
        $query = $repository->createQueryBuilder('t')
            ->select(["t.id", "t.body", "t.state", "t.createdAt", "t.updatedAt"])
            ->where('t.user = :user')
            ->setParameter('user', $user)
            ->orderBy('t.createdAt', 'DESC')
            ->setMaxResults(10)
            ->setFirstResult(($page - 1) * 10 + 1)
            ->getQuery();
        return $query->getResult();
    }

    /**
     *
     * @ApiDoc(
     *     description="タスク削除",
     *     statusCodes={
     *         200="delete complete",
     *         400="taskの情報の不備"
     *         403="Header:X-User-Agent-Authorizationの認証失敗 または　apiKeyの認証失敗"
     *     }
     * )
     * @param int $id
     * @return array
     * @throws \HttpInvalidParamException
     */
    public function deleteTasksAction($id)
    {
        $user = $this->authUser();
        $repository = $this->getDoctrine()->getRepository("AppBundle:Task");
        $task = $repository->find($id);
        if ($task && $user->own($task)) {
            $manager = $this->getDoctrine()->getManager();
            $manager->remove($task);
            $manager->flush();
            return ["message" => "completed delete"];
        }
        throw new \HttpInvalidParamException("invalid task", 400);
    }

}