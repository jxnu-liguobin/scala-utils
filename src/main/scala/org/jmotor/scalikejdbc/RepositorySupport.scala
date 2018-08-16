package org.jmotor.scalikejdbc

import org.jmotor.concurrent.ExecutionContext
import scalikejdbc.{ ConnectionPool, DB, DBSession, using }

import scala.concurrent.{ Future, ExecutionContext ⇒ SExecutionContext }

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
trait RepositorySupport {

  private[this] lazy implicit val ec: SExecutionContext =
    SExecutionContext.fromExecutor(ExecutionContext.lookup("repositories-dispatcher"))

  def readOnly[A](execution: DBSession ⇒ A)(implicit cp: ConnectionPool): Future[A] = {
    execute(execution, db ⇒ db.readOnly[A])
  }

  def autoCommit[A](execution: DBSession ⇒ A)(implicit cp: ConnectionPool): Future[A] = {
    execute(execution, db ⇒ db.autoCommit[A])
  }

  def localTx[A](execution: DBSession ⇒ A)(implicit cp: ConnectionPool): Future[A] = {
    execute(execution, db ⇒ db.localTx[A])
  }

  private[this] def execute[A](execution: DBSession ⇒ A, sessionfn: DB ⇒ (DBSession ⇒ A) ⇒ A)
    (implicit cp: ConnectionPool, ec: SExecutionContext): Future[A] = Future {
    using(DB(cp.borrow())) { db ⇒
      sessionfn(db)(execution)
    }
  }(ec)

}
