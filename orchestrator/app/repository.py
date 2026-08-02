from contextlib import AbstractContextManager
from langgraph.checkpoint.postgres import PostgresSaver
import psycopg

class CheckpointRepository:
    """Owns the PostgreSQL-backed LangGraph checkpointer lifecycle."""
    def __init__(self, database_uri: str):
        with psycopg.connect(database_uri, autocommit=True) as connection:
            connection.execute("CREATE SCHEMA IF NOT EXISTS langgraph")
        separator = "&" if "?" in database_uri else "?"
        checkpoint_uri = f"{database_uri}{separator}options=-csearch_path%3Dlanggraph"
        self._context: AbstractContextManager = PostgresSaver.from_conn_string(checkpoint_uri)
        self.saver = None

    def open(self):
        self.saver = self._context.__enter__()
        self.saver.setup()
        return self.saver

    def close(self) -> None:
        self._context.__exit__(None, None, None)
